package br.com.ajudaqui.service;


import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import br.com.ajudaqui.client.SituacaoCadastralHttpService;
import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.domain.Endereco;
import br.com.ajudaqui.exceptions.AgenciaNaoAtivaOuNaoEncontrada;
import br.com.ajudaqui.repository.AgenciaRepository;
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

  public void deveNaoCadastrarQUandoClientChamarNull() {
    Agencia agencia = agenciaMock();
    Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(null);
    Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontrada.class, () -> agenciaHttpService.cadastrar(agencia));
    Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);

  }

  private Agencia agenciaMock() {
    Endereco endereco = new Endereco(1, "", "", "", 4);
    return new Agencia(3, "", "", "", endereco);

  }
}
