package wmkang.common.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.cache.CodeCache;

@Hidden
@RequiredArgsConstructor
@RequestMapping("/code")
@RestController
public class CodeController {


    private final CodeCache codeCache;


    @GetMapping("/")
    public Response<Map<String, Map<String, String>>> getAllCodeGroups() {
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        codeCache.keys().stream().forEach(k -> resultMap.put(k, codeCache.get(k)));
        return Response.ok(resultMap);
    }

    @GetMapping("/{groupCode}")
    public Response<Map<String, String>> getChildCodes(@PathVariable String groupCode) {
        Map<String, String> map = codeCache.get(groupCode);
        if(map == null)
            return Status.RESOURCE_NOT_EXIST.getResponse(null);
        return Response.ok(map);
    }

    @GetMapping("/{groupCode}/{code}")
    public Response<String> getCodeName(@PathVariable String groupCode, @PathVariable String code) {
        String codeName = codeCache.get(groupCode, code);
        if(codeName == null)
            return Status.RESOURCE_NOT_EXIST.getResponse(null);
        return Response.ok(codeName);
    }
}
