package org.fgaule.platform.travelagent

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class Routes(private val service: TravelAgentService) {

    @GetMapping("/{pnr}")
    @ResponseBody
    fun get(@PathVariable("pnr") pnr: String): TravelPackageDTO {
        return service.get(pnr).fold({ failure -> throw FailureException(failure) }, { a -> TravelPackageTranslator.translate(a) })
    }

}

