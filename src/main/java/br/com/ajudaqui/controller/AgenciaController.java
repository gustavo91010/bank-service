package br.com.ajudaqui.controller;

import org.jboss.resteasy.reactive.RestResponse;

import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.service.AgenciaHttpService;
import io.quarkus.logging.Log;
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
  @Transactional
  public RestResponse<Void> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
    Log.info("[POST] | /agencias | CNPJ: " + agencia.getCnpj());

    this.agenciaHttpService.cadastrar(agencia);
    return RestResponse.created(uriInfo.getAbsolutePath());
  }

  @GET
  @Path("/all")
  public RestResponse<?> all() {
    Log.info("[GET] | /agencias | ");

    return RestResponse.ok(agenciaHttpService.all());
  }

  @GET
  @Path("{id}")
  public RestResponse<Agencia> buscarPorId(Long id) {
    Log.info("[GET] | /agencias | ID: " + id);
    return RestResponse.ok(agenciaHttpService.buscarPorId(id));
  }

  @PUT
  @Path("{id}")
  @Transactional
  public RestResponse<String> alterar(Agencia agencia) {

    Log.info("[PUT] | /agencias | CNPJ: " + agencia.getCnpj());
    agenciaHttpService.alterar(agencia);
    return RestResponse.ok(String.format("Agencia id %s deletada com sucesso!", agencia.getCnpj()));
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public RestResponse<String> deletar(Long id) {
    Log.info("[DELETE] | /agencias | ID: " + id);
    agenciaHttpService.deletar(id);
    return RestResponse.ok(String.format("Agencia id %d deletada com sucesso!", id));
  }

}
