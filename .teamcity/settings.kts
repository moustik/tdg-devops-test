import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.buildCache
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"

project {
    fun cppJob(os: String, preset: String) {
        buildType {
            id("cpp_${os}_${preset}")
            name = "C++ CI - $os ($preset)"

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
                contains("teamcity.agent.jvm.os.name", os.replaceFirstChar { it.uppercase() })
            }

            steps {
                // On the default branch (main), discard any restored build cache so every
                // build is a clean, authoritative compile. Feature branches keep the cache.
                script {
                    name = "[Cache] Discard on default branch"
                    scriptContent = if (os == "linux") {
                        """
                        if [ "%teamcity.build.branch.is_default%" = "true" ]; then
                            rm -rf $buildDir
                        fi
                        """.trimIndent()
                    } else {
                        """
                        if "%teamcity.build.branch.is_default%"=="true" (
                            if exist $buildDir rmdir /s /q $buildDir
                        )
                        """.trimIndent()
                    }
                }

                if (os == "linux") {
                    // Ninja is a system package so cmake won't allow install
                    script {
                        name = "[Agent Setup] Install Ninja"
                        scriptContent = """
                            sudo apt-get update -qq
                            sudo apt-get install -y --no-install-recommends ninja-build
                        """.trimIndent()
                    }
                } else {
                    script {
                        name = "[Agent Setup] Install Build Tools"
                        scriptContent = "pip install --upgrade cmake ninja"
                    }
                }

                script {
                    name = "Configure ($preset)"
                    scriptContent = "cmake --preset $preset"
                }

                script {
                    name = "Build ($preset)"
                    scriptContent = "cmake --build --preset $preset"
                }

                if (preset == "default") {
                    script {
                        name = "Test"
                        scriptContent = "ctest --preset $preset --output-junit ctest-results/results.xml"
                    }
                }
            }

            features {
                // Cache the build directory (compiled objects + GoogleTest _deps/) per branch.
                // TC automatically scopes the cache by branch, so main and feature branches
                // are isolated. publishOnlyChanged avoids redundant uploads on cache hits.
                buildCache {
                    name = "cmake-${os}-${preset}"
                    use = true
                    publish = true
                    publishOnlyChanged = true
                    rules = buildDir
                }

                if (preset == "default") {
                    xmlReport {
                        reportType = XmlReport.XmlReportType.JUNIT
                        rules = "$buildDir/ctest-results/results.xml"
                    }
                }
            }
        }
    }

    cppJob("linux",   "release")
    cppJob("windows", "release")
    cppJob("linux",   "default")
    cppJob("windows", "default")
    cppJob("linux",   "fixed")
    cppJob("windows", "fixed")
}