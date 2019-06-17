package cn.four.steel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.four.steel.bean.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

}
