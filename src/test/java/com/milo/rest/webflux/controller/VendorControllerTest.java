package com.milo.rest.webflux.controller;

import com.milo.rest.webflux.domain.Vendor;
import com.milo.rest.webflux.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


class VendorControllerTest {


    WebTestClient webTestClient;
    VendorRepository repository;
    VendorController controller;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(VendorRepository.class);
        controller = new VendorController(repository);
        webTestClient = WebTestClient.bindToController(controller).build();

    }

    @Test
    void list() {
        given(repository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Fred").lastName("Flintstone").build(),
                        Vendor.builder().firstName("Barney").lastName("Rubble").build()));

        webTestClient.get()
                .uri("/api/v1/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        given(repository.findById("someid"))
                .willReturn(Mono.just(Vendor.builder().firstName("Jimmy").lastName("Johns").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void testCreateVendor() {
        given(repository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("First Name")
                .lastName("Last Name").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateVendor() {
        given(repository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().build());

        webTestClient.put()
                .uri("/api/v1/vendors/someid")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testPatchWithChanges() {
        given(repository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName("Name").build()));
        given(repository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().firstName("New name").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
        verify(repository).save(any());
    }

    @Test
    public void testPatchNoChanges() {
        given(repository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName("Name").build()));
        given(repository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().firstName("Name").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
        verify(repository, never()).save(any());
    }


}