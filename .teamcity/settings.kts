import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.buildCache
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"

project {

fun cppJob(preset: String) {
    buildType {
        id("cpp_linux_${preset}")
        name = "C++ CI - %os% ($preset)"

        val buildDir = if (preset == "release") "build-release" else "build"
        artifactRules = """
            $buildDir/src/app/calculator_app* => artifacts
            $buildDir/src/lib/libcalculator.a => artifacts
            $buildDir/src/lib/calculator.lib => artifacts
        """.trimIndent()

        vcs {
            root(DslContext.settingsRoot)
        }

        triggers {
            vcs {}
        }

        requirements {
            contains("teamcity.agent.jvm.os.name", "%os%")
        }

        steps {
            // On the default branch (main), discard any restored build cache so every
            // build is a clean, authoritative compile. Feature branches keep the cache.
            script {
                name = "[Cache] Discard on default branch (Linux)"
                conditions {
                    equals("os", "Linux")
                }
                scriptContent = """
                    if [ "%teamcity.build.branch.is_default%" = "true" ]; then
                        rm -rf $buildDir
                    fi
                """.trimIndent()
            }

            script {
                name = "[Cache] Discard on default branch (Windows)"
                conditions {
                    equals("os", "Windows")
                }
                scriptContent = """
                    if "%teamcity.build.branch.is_default%"=="true" (
                        if exist $buildDir rmdir /s /q $buildDir
                    )
                """.trimIndent()
            }

            // Ninja is a system package so cmake won't allow install
            script {
                name = "[Agent Setup] Install Ninja (Linux)"
                conditions {
                    equals("os", "Linux")
                }
                scriptContent = """
                    sudo apt-get update -qq
                    sudo apt-get install -y --no-install-recommends ninja-build
                """.trimIndent()
            }

            script {
                name = "[Agent Setup] Install Build Tools (Windows)"
                conditions {
                    equals("os", "Windows")
                }
                scriptContent = "pip install --upgrade cmake ninja"
            }

            script {
                name = "Configure ($preset)"
                scriptContent = "cmake --preset $preset"
            }

            script {
                name = "Build ($preset)"
                scriptContent = "cmake --build --preset $preset"
            }

            if (preset != "release") {
                script {
                    name = "Test"
                    scriptContent = "ctest --preset $preset --output-junit ctest-results/results.xml"
                }
            }
        }

        features {
            matrix {
                os = listOf(
                    value("Linux"),
                    value("Windows")
                )
                        
                // instructs TeamCity to place individual builds artifacts under the sub directories corresponding to builds' matrix parameter values
                groupArtifactsByBuild = true
            }

            // Cache the build directory (compiled objects + GoogleTest _deps/) per branch.
            // TC automatically scopes the cache by branch, so main and feature branches
            // are isolated. publishOnlyChanged avoids redundant uploads on cache hits.
            buildCache {
                name = "cmake-%os%-${preset}"
                use = true
                publish = true
                publishOnlyChanged = true
                rules = buildDir
            }

            if (preset != "release") {
                xmlReport {
                    reportType = XmlReport.XmlReportType.JUNIT
                    rules = "$buildDir/ctest-results/results.xml"
                }
            }
        }
    }
}

    cppJob("release")
    cppJob("default")
    cppJob("fixed")

}
