# Setup Visual Studio Code for OpenJDK/HotSpot development

Description of how to setup VSC for native HotSpot development in C++ on a relatively old Linux OS like Ubuntu 14.04.

## Download VSC

VSC binaries can be downloaded from https://code.visualstudio.com/download. I downloaded and extracted the `tar.gz` archive of version 1.38 which runs just fine on Ubuntu 14.04.

## VSC C++ extensions

There exists a [Microsoft C/C++ extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode.cpptools) for VSC with a lot of [fancy features](https://code.visualstudio.com/docs/languages/cpp) but this extensions is still missing "call hierarchies" - a feature which I consider essential for browsing big code bases like HotSpot.

While there is work underway to implement call hierarchies for the Microsoft VSC C++ extension (see [Call Hierarchy #468](https://github.com/microsoft/language-server-protocol/issues/468) and [ Call hierarchy #16 ](https://github.com/microsoft/vscode-cpptools/issues/16)) I decided to use the alternative [`ccls`](https://marketplace.visualstudio.com/items?itemName=ccls-project.ccls) extension which brings C++ support to VSC based on the [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) (LSP) and by leveraging the [Clang](https://clang.llvm.org/) compiler infrastructure through `libLLVM`/[`libclang`](https://clang.llvm.org/doxygen/group__CINDEX.html).

Notice that there exist other LSP implementations based on Clang. First of all there's [`clangd`](https://clang.llvm.org/extra/clangd/) which is a part of the Clang project itself and second there's [`cquery`](https://github.com/cquery-project/cquery) which can be considered as a direct predecessor of `ccls`. While `cquery` doesn't seem to be actively developed any more, `clangd` is less feature rich when compared to `ccls` (e.g. it doesn't support call hierarchies).

## Building and installing `ccls`

`ccls` must be build from source. The various build dependencies are listed in the [`ccls` build wiki](https://github.com/MaskRay/ccls/wiki/Build). First I had to install a decent version of [CMake](https://cmake.org). [`cmake-3.15.3-Linux-x86_64.tar.gz`](https://github.com/Kitware/CMake/releases/download/v3.15.3/cmake-3.15.3-Linux-x86_64.tar.gz) worked fine on my Ubuntu 14.04.

As a current version of the Clang/LLVM libraries and headers is obviously required for `ccls` anyway, I decided to use `clang++` as C++ compiler and installed a complete Clang/LLVM binary release from the [LLVM download page](http://releases.llvm.org/download.html). It turns out the the Suse 11 version [clang+llvm-8.0.1-x86_64-linux-sles11.3.tar.xz](https://github.com/llvm/llvm-project/releases/download/llvmorg-8.0.1/clang+llvm-8.0.1-x86_64-linux-sles11.3.tar.xz) works perfectly fine on Ubuntu 14.04 as well. I've unpacked it into a local directory which I'll subsequently reference with  `$CLANG-LLVM-PATH`

```
$ git clone --depth=1 --recursive https://github.com/MaskRay/ccls
$ cd ccls
```

```
$ /share/software/cmake-3.15.3-Linux-x86_64/bin/cmake -H. -BRelease -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_FLAGS='-stdlib=libc++' -DCMAKE_EXE_LINKER_FLAGS='-Wl,-rpath,/share/software/clang+llvm-8.0.1-x86_64-linux-sles11.3/lib -stdlib=libc++ -lc++abi' -DCMAKE_CXX_COMPILER=/share/software/clang+llvm-8.0.1-x86_64-linux-sles11.3/bin/clang++ -DCMAKE_PREFIX_PATH=/share/software/clang+llvm-8.0.1-x86_64-linux-sles11.3
$ /share/software/cmake-3.15.3-Linux-x86_64/bin/cmake --build Release --verbose 2>&1 | tee build_new.log
```

### Building on RHEL 8.2

Install the following packages:
```
sudo yum install cmake
sudo yum install clang-devel
sudo yum install llvm-devel
```
Afterwards do:
```
$ git clone --depth=1 --recursive https://github.com/MaskRay/ccls
$ cd ccls
$ VERBOSE=1 cmake --build Release
```
This will build everything up until the link process which will fail with:
```
/usr/bin/ld: cannot find -lclangIndex
/usr/bin/ld: cannot find -lclangFormat
/usr/bin/ld: cannot find -lclangTooling
/usr/bin/ld: cannot find -lclangToolingInclusions
/usr/bin/ld: cannot find -lclangToolingCore
/usr/bin/ld: cannot find -lclangFrontend
/usr/bin/ld: cannot find -lclangParse
/usr/bin/ld: cannot find -lclangSerialization
/usr/bin/ld: cannot find -lclangSema
/usr/bin/ld: cannot find -lclangAST
/usr/bin/ld: cannot find -lclangLex
/usr/bin/ld: cannot find -lclangDriver
/usr/bin/ld: cannot find -lclangBasic
```
But `VERBOSE=1` will give us the exact link command. Now replace `-lclangIndex -lclangFormat -lclangTooling -lclangToolingInclusions -lclangToolingCore -lclangFrontend -lclangParse -lclangSerialization -lclangSema -lclangAST -lclangLex -lclangDriver -lclangBasic` in the link command by `-lclang-cpp` and re-run the link command manually. This will successfully create `./Release/ccls`.
