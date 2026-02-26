import os

from conan import ConanFile
from conan.tools.cmake import CMake, CMakeToolchain, cmake_layout, CMakeDeps
from conan.tools.files import copy


class TdgCalculatorConan(ConanFile):
    name = "tdg-calculator"
    description = "TDG DevOps Test - Calculator library (C++17)"
    license = "Proprietary"
    package_type = "static-library"

    settings = "os", "compiler", "build_type", "arch"
    options = {"fPIC": [True, False]}
    default_options = {"fPIC": True}

    # package only needs library sources and CMake
    exports_sources = "CMakeLists.txt", "src/lib/*"

    def set_version(self):
        # TC_BUILD_NUMBER is set automatically by TeamCity.
        # Locally, set it manually or leave unset to get the dev fallback.
        self.version = os.environ.get("TC_BUILD_NUMBER", "0.0.0-dev")

    def config_options(self):
        if self.settings.os == "Windows":
            self.options.rm_safe("fPIC")

    def layout(self):
        cmake_layout(self)

    def generate(self):
        deps = CMakeDeps(self)
        deps.generate()
        tc = CMakeToolchain(self)
        # Pass options not to build App or Tests
        tc.variables["BUILD_APP"] = False
        tc.variables["BUILD_TESTS"] = False
        tc.generate()

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build(target="calculator")

    def package(self):
        copy(self, "*.h",
             src=os.path.join(self.source_folder, "src", "lib"),
             dst=os.path.join(self.package_folder, "include"))
        # Catch both .a (Linux/MinGW) and .lib (MSVC)
        copy(self, "*.a",
             src=self.build_folder,
             dst=os.path.join(self.package_folder, "lib"),
             keep_path=False)
        copy(self, "*.lib",
             src=self.build_folder,
             dst=os.path.join(self.package_folder, "lib"),
             keep_path=False)

    def package_info(self):
        self.cpp_info.libs = ["calculator"]
