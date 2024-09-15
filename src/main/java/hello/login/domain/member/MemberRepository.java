package hello.login.domain.member;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MemberRepository {

    private static final Map<Long, Member> stores = new HashMap<>();
    private static long sequence = 0L;


    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save : member = {}", member);
        stores.put(member.getId(), member);
        return member;
    }


    public Member findById(Long id) {
        return stores.get(id);
    }


    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream().filter(member -> member.getLoginId().equals(loginId)).findFirst();
    }


    public List<Member> findAll() {
        return new ArrayList<>(stores.values());
    }


    public void clear() {
        stores.clear();
    }
}
