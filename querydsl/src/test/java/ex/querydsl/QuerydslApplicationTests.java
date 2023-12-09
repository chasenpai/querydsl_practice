package ex.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.entity.Hello;
import ex.querydsl.entity.QHello;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {

		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;

		Hello findHello = queryFactory.selectFrom(qHello).fetchOne();
		assertThat(findHello).isEqualTo(hello);
		assertThat(findHello.getId()).isEqualTo(hello.getId());
	}

}
