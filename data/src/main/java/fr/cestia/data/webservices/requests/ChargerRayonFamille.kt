package fr.cestia.data.webservices.requests

import fr.cestia.common_files.ConfigurationGenerale.CODE_CLIENT
import fr.cestia.common_files.ConfigurationGenerale.CODE_ENTREPRISE

class ChargerRayonFamille : BaseRequest() {
    override fun getBody(): String {
        return """
            <tem:ChargerRayonFamille>
                <tem:codeClient>$CODE_CLIENT</tem:codeClient>
                <tem:codeEntreprise>$CODE_ENTREPRISE</tem:codeEntreprise>
            </tem:ChargerRayonFamille>
        """.trimIndent()
    }
}