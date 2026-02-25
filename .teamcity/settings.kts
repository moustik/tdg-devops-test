import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.buildCache
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"


fun Project.buildJob(preset: String) {
    buildType {
        id("pipeline_${preset}")
        name = "Calculator CI - %os% ($preset)"

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
            contains("teamcity.agent.name", "%os%-Small")
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
                        
                // use `os` subdir for artifacts
                groupArtifactsByBuild = true
            }

            // Cache the build directory per branch.
            buildCache {
                name = "cmake-%os%-${preset}"
                use = true
                publish = true
                publishOnlyChanged = true
                rules = buildDir
            }

            // Release preset has not tests
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
