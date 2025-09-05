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
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AgenciaHttpService {

  @RestClient
  private SituacaoCadastralHttpService situacaoCadastralHttpService;
  private final AgenciaRepository agenciaRepository;

  public AgenciaHttpService(AgenciaRepository agenciaRepository) {
    this.agenciaRepository = agenciaRepository;
  }

  // private List<Agencia> agencias = new ArrayList<>();

  public void cadastrar(Agencia agencia) {
    System.out.println(
      "estamo dentro do service j;a..."
    );
    AgenciaHttp buscarPorCnpj = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
    if (buscarPorCnpj.getSituacaoCadastral() != null
        && buscarPorCnpj.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
      // agencias.add(agencia);
  
      agenciaRepository.persist(agencia);
    } else {
      throw new AgenciaNaoAtivaOuNaoEncontrada();

    }

  }

  public List<Agencia> all(){
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
