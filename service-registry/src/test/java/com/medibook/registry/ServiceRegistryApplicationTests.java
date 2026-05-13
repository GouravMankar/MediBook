package com.medibook.registry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ServiceRegistryApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertThat(ServiceRegistryApplication.class).isNotNull();
    }
}
