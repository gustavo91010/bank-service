package br.com.ajudaqui.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.ajudaqui.client.AgenciaHttp;
import br.com.ajudaqui.client.SituacaoCadastralHttpService;
import br.com.ajudaqui.domain.Agencia;
import br.com.ajudaqui.exceptions.AgenciaNaoAtivaOuNaoEncontrada;
import br.com.ajudaqui.repository.AgenciaRepository;
import br.com.ajudaqui.utils.SituacaoCadastral;
import io.micrometer.core.instrument.MeterRegistry;
// import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
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

  @WithTransaction // para manter a transação aberta
  @CircuitBreaker(requestVolumeThreshold = 5, // quantidade de vezes ate abriri o circuito
      failureRatio = 0.5, // porcentagem de erro
      delay = 2000, // tempo para ficar rauf open
      successThreshold = 2 // numero de tentativas para fechar o cirvuti
  )
  @Fallback(fallbackMethod = "chamarFallbackMethod")
  public Uni<Void> cadastrar(Agencia agencia) {
    Uni<AgenciaHttp> buscarPorCnpj = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());

    return buscarPorCnpj.onItem().ifNull().failWith(new AgenciaNaoAtivaOuNaoEncontrada())
        .onItem().transformToUni(item -> persistirSeAtiva(agencia, item));
  }

  public Uni<Void> chamarFallbackMethod(Agencia agencia) {
    Log.info(String.format("Agencica com cnpj %s não foi adicionada", agencia.getCnpj()));
    return Uni.createFrom().nullItem();
  }

  private Uni<Void> persistirSeAtiva(Agencia agencia, AgenciaHttp item) {

    meterRegistry.counter("Incremento na chamada da agencia").increment();
    if (agencia == null ||
        item.getSituacaoCadastral() == null
            && item.getSituacaoCadastral().equals(SituacaoCadastral.INATIVO)) {
      meterRegistry.counter("Chamada de agencia com erro").increment();
      // throw new AgenciaNaoAtivaOuNaoEncontrada();
      return Uni.createFrom().failure(new AgenciaNaoAtivaOuNaoEncontrada());
    }
    meterRegistry.counter("Chamada de agencia com sucesso").increment();
    return agenciaRepository.persist(agencia).replaceWithVoid();

  }

  @WithTransaction // para manter a transação aberta
  public Uni<List<Agencia>> all() {
    return agenciaRepository.findAll().list();
  }

  @WithTransaction // para manter a transação aberta
  public Uni<Agencia> buscarPorId(Long id) {
    return agenciaRepository.findById(id);
    // return agencias.stream()
    // .filter(a -> a.getId().equals(id))
    // .toList()
    // .getFirst();
  }

  @WithTransaction // para manter a transação aberta com a base de dados
  public Uni<Void> deletar(Long id) {
    return agenciaRepository.deleteById(id).replaceWithVoid();
    // agencias.removeIf(a -> a.getId().equals(id));
  }

  @WithTransaction // para manter a transação aberta com a base de dados
  public Uni<Void> alterar(Agencia agencia) {
    return agenciaRepository.update("nome =?1, razaoSocial = ?2, cnpj = ?3 where id = ?4",
        agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId()).replaceWithVoid();
    // deletar(agencia.getId());
    // cadastrar(agencia);
  }
}
