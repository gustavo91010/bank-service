package br.com.ajudaqui.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.ajudaqui.client.AgenciaHttp;
import br.com.ajudaqui.client.SituacaoCadastralHttpService;
import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.domain.Endereco;
import br.com.ajudaqui.exceptions.AgenciaNaoAtivaOuNaoEncontrada;
import br.com.ajudaqui.repository.AgenciaRepository;
import br.com.ajudaqui.utils.SituacaoCadastral;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class AgenciaHttpServiceTest {
  @InjectMock
  private AgenciaRepository agenciaRepository;

  @InjectMock
  @RestClient
  private SituacaoCadastralHttpService situacaoCadastralHttpService;

  @Inject
  private AgenciaHttpService agenciaHttpService;

  @Test
  public void deveNaoCadastrarQUandoClientChamarNull() {
    Agencia agencia = agenciaMock();
    Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(null);
    Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontrada.class, () -> agenciaHttpService.cadastrar(agencia));
    Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
  }

  @Test
  void deveCadastraQuandoRetornarSituacaoCadastralAtiva() {
    Agencia agencia = agenciaMock();
    Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123"))
        .thenReturn(agenciaHttpMock(SituacaoCadastral.ATIVO));
    agenciaHttpService.cadastrar(agencia);
    Mockito.verify(agenciaRepository).persist(agencia);
  }

  private AgenciaHttp agenciaHttpMock(SituacaoCadastral situacaiCadastral) {
    return new AgenciaHttp("", "", "", situacaiCadastral);
  }

  private Agencia agenciaMock() {
    Endereco endereco = new Endereco(1, "", "", "", 4);
    return new Agencia(3, "", "", "123", endereco);
  }
}
