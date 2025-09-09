package br.com.ajudaqui.domain;

import java.util.List;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
// import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Person extends PanacheEntity {
  public String name;
  public int age;

  public static Person register(String name, int age) {
    Person p = new Person();
    p.name = name;
    p.age = age;
    p.persist();
    return p;
  }

  public static Uni<Person> findByName(String name) {
    return find("name", name).firstResult();
  }

  public static Uni<Long> userReistered() {
    return count();
  }

  public static Uni<List<PanacheEntityBase>> findAdults() {

    return list("age >= 18", 18);
  }

  // findAll() → retorna todos os registros.
  // findById(id) → busca pelo id.
  // count() → retorna a quantidade de registros.
  // deleteById(id) → deleta por id.
  // delete("campo", valor) → deleta por condição.
  // stream("condição") → retorna um Stream em vez de lista.
}

// Active Record:
// A lógica de persistência e as regras de negócio estão todas dentro da
// entidade. Isso facilita a leitura,
// mas pode dificultar a manutenção, já que a entidade carrega ambas as
// responsabilidades.
