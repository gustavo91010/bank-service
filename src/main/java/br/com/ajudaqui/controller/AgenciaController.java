package br.com.ajudaqui.controller;

import org.jboss.resteasy.reactive.RestResponse;

import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.service.AgenciaHttpService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/agencias")
public class AgenciaController {

  private AgenciaHttpService agenciaHttpService;

  public AgenciaController(AgenciaHttpService agenciaHttpService) {
    this.agenciaHttpService = agenciaHttpService;
  }

  @POST
  public RestResponse<Void> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
    this.agenciaHttpService.cadastrar(agencia);
    return RestResponse.created(uriInfo.getAbsolutePath());
  }

  // @GET
  // @Path("{id}")
  // public RestResponse<String> buscarPorId(String id) {
  //   return RestResponse.ok("ha! " + id);
  // }

  @GET
  @Path("{id}")
  public RestResponse<Agencia> buscarPorId(Long id) {
    return RestResponse.ok(agenciaHttpService.buscarPorId(id));
  }

  @PUT
  @Path("{id}")
  public RestResponse<Void> alterar(Agencia agencia) {
    agenciaHttpService.alterar(agencia);
    return RestResponse.ok();
  }

  @DELETE
  @Path("{id}")
  public RestResponse<Void> deletar(Long id) {
    agenciaHttpService.deletar(id);
    return RestResponse.ok();
  }

}
