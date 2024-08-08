package kr.co.yohancompany.my_restful_service.repository;

import kr.co.yohancompany.my_restful_service.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {


}
