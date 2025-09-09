package br.com.ajudaqui.controller;

import org.jboss.resteasy.reactive.RestResponse;

import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.service.AgenciaHttpService;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/agencias")
public class AgenciaController {

  private AgenciaHttpService agenciaHttpService;

  public AgenciaController(AgenciaHttpService agenciaHttpService) {
    this.agenciaHttpService = agenciaHttpService;
  }

  @POST
  @NonBlocking
  @Transactional
  public Uni<?> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
    Log.info("[POST] | /agencias | CNPJ: " + agencia.getCnpj());
    return this.agenciaHttpService.cadastrar(agencia)
        .replaceWith(RestResponse.created(uriInfo.getAbsolutePathBuilder().build()));

  }

  @GET
  @Path("/all")
  public RestResponse<?> all() {
    Log.info("[GET] | /agencias | ");

    return RestResponse.ok(agenciaHttpService.all());
  }

  @GET
  @Path("{id}")
  public Uni<RestResponse<Agencia>> buscarPorId(Long id) {
    Log.info("[GET] | /agencias | ID: " + id);
    return agenciaHttpService.buscarPorId(id).onItem().transform(RestResponse::ok);
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Uni<RestResponse<String>> alterar(Agencia agencia) {

    Log.info("[PUT] | /agencias | CNPJ: " + agencia.getCnpj());
    return agenciaHttpService.alterar(agencia)
        .replaceWith(RestResponse.ok(String.format("Agencia id %s deletada com sucesso!", agencia.getCnpj())));
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Uni<RestResponse<String>> deletar(Long id) {
    Log.info("[DELETE] | /agencias | ID: " + id);
    return agenciaHttpService.deletar(id)
        .replaceWith(RestResponse.ok(String.format("Agencia id %d deletada com sucesso!", id)));
  }

}
