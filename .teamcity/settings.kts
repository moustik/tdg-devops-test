import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.buildCache
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"


fun Project.buildJob(preset: String) {
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
            buildSteps(preset, buildDir)
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


project {
    buildJob("release")
    buildJob("default")
    buildJob("fixed")
}
