package com.ironhack.midterm.repository;


import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountOneIdAndAccountOneType(Long accountOneId, AccountType accountOneType);

    @Query(
            value = "with x as (\n" +
                    "SELECT sum(amount) as total\n" +
                    "from transaction\n" +
                    "where (account_one_id = :id OR account_two_id = :id) AND (transaction_date NOT IN (\n" +
                    "  SELECT transaction_date \n" +
                    "  FROM transaction\n" +
                    "  WHERE transaction_date = CURDATE()))\n" +
                    "group by transaction_date\n" +
                    ")\n" +
                    "select max(total) from x",
            nativeQuery = true
    )
    Optional<Double> findMaxAmount(@Param("id") String accountId);

    @Query(
            value = "SELECT sum(amount)\n" +
                    "from transaction\n" +
                    "where (account_one_id = :id AND transaction_date = :transactiondate) OR (account_two_id = :id AND transaction_date = :transactiondate)",
            nativeQuery = true
    )
    Optional<Double> findTotalToday(@Param("id") String accountId, @Param("transactiondate") LocalDateTime date);

    @Query(
            value = "SELECT amount, transaction_date\n" +
                    "from transaction\n" +
                    "where (account_one_id = :id AND transaction_date = :transactiondate) OR (account_two_id = :id AND transaction_date = :transactiondate)",
            nativeQuery = true
    )
    List<Transaction> findAllTransactionsFromId(@Param("id") String accountId, @Param("transactiondate")LocalDateTime date);

    @Query(
            value = "SELECT amount, transaction_date\n" +
                    "from transaction\n" +
                    "where transaction_date = :date",
            nativeQuery = true
    )
    List<Transaction> findAllTransactionsFromTime(@Param("date") LocalDateTime date);
}

