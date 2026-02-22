import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"

project {
    fun cppJob(os: String, preset: String) {
        buildType {
            id("cpp_${os}_${preset}")
            name = "C++ CI - $os ($preset)"

            val buildDir = if (preset == "release") "build-release" else "build"
            artifactRules = "$buildDir/src/app/calculator_app* => artifacts"

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

            if (preset == "default") {
                features {
                    xmlReport {
                        reportType = XmlReport.XmlReportType.JUNIT
                        rules = "$buildDir/ctest-results/results.xml"
                    }
                }
            }
        }
    }

    cppJob("linux",   "default")
    cppJob("windows", "default")
}