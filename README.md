# TDG DevOps Test Project

A C++17 calculator application with a library and unit tests (Google Test).

## Prerequisites

- CMake 3.14+
- Ninja
- A C++17 compatible compiler (GCC, Clang, MSVC)

## Build

### Using CMake Presets

**Debug (with tests):**

```bash
cmake --preset default
cmake --build --preset default
```

**Release (without tests):**

```bash
cmake --preset release
cmake --build --preset release
```

### Manual

```bash
cmake -B build -G Ninja -DCMAKE_BUILD_TYPE=Debug -DBUILD_TESTS=ON
cmake --build build
```

## Run Tests

```bash
ctest --preset default
```

Or directly:

```bash
./build/tests/calculator_tests
```

## Run the Application

```bash
./build/src/app/calculator_app
```
