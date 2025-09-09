package br.com.ajudaqui.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/situacao-cadastral")
@RegisterRestClient(configKey = "situacao-cadastral-api")
public interface SituacaoCadastralHttpService {

  @GET
  @Path("{cnpj}")
  Uni<AgenciaHttp> buscarPorCnpj(@PathParam("cnpj") String cnpj);

}
