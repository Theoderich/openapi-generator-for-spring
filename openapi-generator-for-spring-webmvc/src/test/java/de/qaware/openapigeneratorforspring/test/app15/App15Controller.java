package de.qaware.openapigeneratorforspring.test.app15;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class App15Controller {

    @GetMapping("/mapping1")
    @ApiResponse(description = "Description from method")
    @Operation(
            responses = {
                    @ApiResponse(description = "Description from operation")
            },
            parameters = {
                    @Parameter(name = "param1", description = "Description from operation", in = ParameterIn.PATH)
            }
    )
    @Parameter(name = "param2", description = "Description from method", in = ParameterIn.COOKIE)
    public void mapping1(@RequestParam @Parameter(description = "Description from param") String param3) {

    }

    @GetMapping("/mapping2")
    @ApiResponse(description = "Description from operation")
    public void mapping2() {

    }


}
