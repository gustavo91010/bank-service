package br.com.ajudaqui.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.ajudaqui.client.AgenciaHttp;
import br.com.ajudaqui.client.SituacaoCadastralHttpService;
import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.exceptions.AgenciaNaoAtivaOuNaoEncontrada;
import br.com.ajudaqui.repository.AgenciaRepository;
import br.com.ajudaqui.utils.SituacaoCadastral;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AgenciaHttpService {

  @RestClient
  private SituacaoCadastralHttpService situacaoCadastralHttpService;
  private final AgenciaRepository agenciaRepository;
  private final MeterRegistry meterRegistry;

  public AgenciaHttpService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
    this.agenciaRepository = agenciaRepository;
    this.meterRegistry = meterRegistry;
  }

  public void cadastrar(Agencia agencia) {
    AgenciaHttp buscarPorCnpj = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
    meterRegistry.counter("Incremento na chamada da agencia").increment();
    if (buscarPorCnpj == null ||
        buscarPorCnpj.getSituacaoCadastral() == null
            && !buscarPorCnpj.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
      meterRegistry.counter("Chamada de agencia com erro").increment();
      throw new AgenciaNaoAtivaOuNaoEncontrada();
    }
    meterRegistry.counter("Chamada de agencia com sucesso").increment();
    agenciaRepository.persist(agencia);
  }

  public List<Agencia> all() {
    return agenciaRepository.findAll().list();
  }

  public Agencia buscarPorId(Long id) {
    return agenciaRepository.findById(id);
    // return agencias.stream()
    // .filter(a -> a.getId().equals(id))
    // .toList()
    // .getFirst();
  }

  public void deletar(Long id) {
    agenciaRepository.deleteById(id);
    // agencias.removeIf(a -> a.getId().equals(id));
  }

  public void alterar(Agencia agencia) {
    agenciaRepository.update("nome =?1, razaoSocial = ?2, cnpj = ?3 where id = ?4",
        agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId());
    // deletar(agencia.getId());
    // cadastrar(agencia);
  }
}
