cmake_minimum_required(VERSION 3.10.2)

#project("mycrosscompile")

file(GLOB allCPP *.cpp)

add_library( # Sets the name of the library.
             native-lib
             SHARED
             ${allCPP} )

find_library( # Sets the name of the path variable.
              log-lib
              log )

## 导入静态库,这里的import是一个标识
add_library(getStaticNdk STATIC IMPORTED)

set_target_properties(getStaticNdk PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libgetndka.a)

## 导入动态库,这里的import是一个标识
#add_library(getSharedNdk SHARED IMPORTED)

#set_target_properties(getSharedNdk PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLib/arm64-v8a/libgetndkso.so)

target_link_libraries( # Specifies the target library.
        native-lib
        ${log-lib}
        getStaticNdk
        )