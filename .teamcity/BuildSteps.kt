import jetbrains.buildServer.configs.kotlin.BuildSteps
import jetbrains.buildServer.configs.kotlin.buildSteps.script

fun BuildSteps.buildSteps(preset: String, buildDir: String) {
    // On the default branch (main), discard any restored build cache
    // so we get a clean build. Feature branches keep the cache.
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
