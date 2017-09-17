package com.springms.cloud.repository;


import com.springms.cloud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户DAO文件</br>。
 *
 * <li>@Repository 加这个注解表示这个文件是一个DAO文件</li>
 *
 * @author HEHUI231
 *
 * @version 0.0.1
 *
 * @date 2017/8/13
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
