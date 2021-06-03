package wmkang.task;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import wmkang.domain.enums.Role;
import wmkang.domain.enums.Shard;
import wmkang.domain.manage.entity.Code;
import wmkang.domain.manage.entity.CodeGroup;
import wmkang.domain.manage.repository.CodeGroupRepository;
import wmkang.domain.manage.repository.CodeRepository;
import wmkang.task.controller.OpenUserController;
import wmkang.task.dto.OpenUserControllerDto.RegistUser;


@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {


    private final OpenUserController  openUserController;

    private final CodeGroupRepository codeGroupRepo;
    private final CodeRepository      codeRepo;


    @Override
    public void run(String... args) throws Exception {

        // 권한별 사용자 생성
        openUserController.register(new RegistUser("wmkang@wmkang.com",  "abc123!@#", "abc123!@#", "강위민", Role.USER,    Shard.FIRST));
        openUserController.register(new RegistUser("manager@wmkang.com", "abc123!@#", "abc123!@#", "관리자", Role.MANAGER, Shard.FIRST));
        openUserController.register(new RegistUser("admin@wmkang.com",   "abc123!@#", "abc123!@#", "어드민", Role.ADMIN,   Shard.FIRST));

        // 공통코드
        codeGroupRepo.save(new CodeGroup("EXT", "파일확장자"));
            codeRepo.save(new Code("EXT", "PPT", "파워포인트문서"));
            codeRepo.save(new Code("EXT", "XLS", "엑셀문서"));
            codeRepo.save(new Code("EXT", "DOC", "워드문서"));
            codeRepo.save(new Code("EXT", "ZIP", "압축파일"));
            codeRepo.save(new Code("EXT", "EXE", "윈도우실행파일"));
            codeRepo.save(new Code("EXT", "MOV", "동영상파일"));

        codeGroupRepo.save(new CodeGroup("CAT", "카테고리"));
            codeRepo.save(new Code("CAT", "GAM", "게임"));
            codeRepo.save(new Code("CAT", "MOV", "영화"));
            codeRepo.save(new Code("CAT", "MSC", "음악"));
            codeRepo.save(new Code("CAT", "MUS", "뮤지컬"));
            codeRepo.save(new Code("CAT", "SPT", "스포츠"));
    }
}
