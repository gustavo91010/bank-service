package br.com.ajudaqui.repository;

import br.com.ajudaqui.domain.Agencia;

// import io.quarkus.hibernate.orm.panache.PanacheRepository; // import para paradigma imperaticco
import io.quarkus.hibernate.reactive.panache.PanacheRepository; // IMprt para paradigma raetivo
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgenciaRepository implements PanacheRepository<Agencia>{

  
}
