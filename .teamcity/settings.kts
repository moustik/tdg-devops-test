import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"

project {
    fun cppJob(os: String, preset: String) {
        buildType {
            id("cpp_${os}_${preset}")
            name = "C++ CI - $os ($preset)"

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
        }
    }

    cppJob("linux",   "default")
    cppJob("linux",   "release")
    cppJob("windows", "default")
    cppJob("windows", "release")
}