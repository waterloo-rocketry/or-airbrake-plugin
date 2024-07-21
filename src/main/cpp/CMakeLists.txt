cmake_minimum_required(VERSION 3.27.7)

set_source_files_properties(cansw_processor_stm/STM32Cube/Calculations/drag.c PROPERTIES LANGUAGE CXX)
add_library(processor_calculations SHARED
    cansw_processor_stm/STM32Cube/Calculations/drag.c
    processor_calculations.cpp
)
set_target_properties(processor_calculations PROPERTIES LINK_FLAGS "-Wl,--add-stdcall-alias")
