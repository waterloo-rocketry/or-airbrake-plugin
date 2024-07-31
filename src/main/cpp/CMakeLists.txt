cmake_minimum_required(VERSION 3.27.7)

set_source_files_properties(cansw_processor_stm/STM32Cube/Calculations/controller_lib.c PROPERTIES LANGUAGE CXX)
set_source_files_properties(cansw_processor_stm/STM32Cube/Calculations/trajectory_lib.c PROPERTIES LANGUAGE CXX)
add_library(processor_calculations SHARED
    cansw_processor_stm/STM32Cube/Calculations/controller_lib.c
    cansw_processor_stm/STM32Cube/Calculations/trajectory_lib.c
    processor_calculations.cpp
)
IF (WIN32)
  set_target_properties(processor_calculations PROPERTIES LINK_FLAGS "-Wl,--add-stdcall-alias")
ENDIF()

