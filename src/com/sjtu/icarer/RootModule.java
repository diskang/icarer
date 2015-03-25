package com.sjtu.icarer;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module(
        includes = {
                AndroidModule.class,
                IcarerModule.class
        }
)
public class RootModule {
}
