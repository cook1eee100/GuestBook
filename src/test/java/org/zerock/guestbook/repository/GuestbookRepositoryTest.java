package org.zerock.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GuestbookRepositoryTest {

    @Autowired
    private GuestbookRepository guestbookRepository;

    // 데이터 삽입 Test
    @Test
    public void insertDummies(){

        IntStream.rangeClosed(1, 300).forEach(i->{
            Guestbook guestbook = Guestbook.builder()
                    .title("Title...."+i)
                    .content("Content..." + i)
                    .writer("user"+(i%10))
                    .build();

            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    // 데이터 수정 Test
    @Test
    public void updateTest(){
        Optional<Guestbook> result=guestbookRepository.findById(600L);  // 존재하는 번호로 테스트

        if(result.isPresent()){
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title....");
            guestbook.changeContent("Changed Content...");

            guestbookRepository.save(guestbook);
        }
    }

    // 단일 항목 검색 Test    ( title에 "1" 인 keyword가 존재하는 것)
    @Test
    public void testQuery1(){
        Pageable pageable= PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword="1";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expression = qGuestbook.title.contains(keyword);
        builder.and(expression);
        Page<Guestbook> result=guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook->{
            System.out.println(guestbook);
        });
    }

    // 다중 항목 검색 Test ( title 이나 content 에 "1" 인 keyword가 존재하면서 gno이 0보다 큰 것)
    @Test
    public void testQuery2(){
        Pageable pageable = PageRequest.of(1, 10, Sort.by("gno").descending());
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword="1";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression exTitle=qGuestbook.title.contains(keyword);
        BooleanExpression exContent=qGuestbook.content.contains(keyword);
        BooleanExpression exAll=exTitle.or(exContent);
        builder.and(exAll);
        builder.and(qGuestbook.gno.gt(0L));

        Page<Guestbook> result=guestbookRepository.findAll(builder, pageable);
        result.stream().forEach(guestbook->{
            System.out.println(guestbook);
        });

    }
}