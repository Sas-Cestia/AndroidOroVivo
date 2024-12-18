package fr.cestia.data.webservices

import android.util.Log
import fr.cestia.data.models.inventaire.QteInventaireVitrine
import fr.cestia.data.models.produit.Famille
import fr.cestia.data.models.produit.Matiere
import fr.cestia.data.webservices.requests.ChargerQteInventaireVitrine
import fr.cestia.data.webservices.requests.ChargerRayonFamille
import fr.cestia.data.webservices.soapservice.SoapService
import org.jsoup.Jsoup
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class RemoteDataSourceImpl(
    private val soapService: SoapService,
) : RemoteDataSource {

    // Fonction pour extraire la faultstring à partir de la réponse SOAP
    private fun getFaultString(soapResponse: String): String? {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val inputSource = InputSource(StringReader(soapResponse))
            val doc: Document = builder.parse(inputSource)

            // Recherche de l'élément <faultstring>
            val faultStringNode = doc.getElementsByTagName("faultstring").item(0)

            // Retourner le texte à l'intérieur de <faultstring>
            faultStringNode?.textContent
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Erreur lors de l'extraction de l'erreur retournée par le webservice.")
        }
    }

    override suspend fun fetchMatieresFamilles(): Pair<List<Matiere>, List<Famille>> {
        // Génère l'enveloppe SOAP
        val request = ChargerRayonFamille().buildRequest()

        // Appelle le webservice
        val response = soapService.chargerRayonFamille(request)

        if (response.isSuccessful) {
            val responseBody = response.body()
                ?: throw Exception("La réponse du webservice ChargerRayonFamille est vide")
            return parseChargerRayonFamilleResponse(responseBody)
        } else {
            val soapErrorResponse = response.errorBody()?.string()
            var faultString: String? = null

            soapErrorResponse?.let {
                faultString = getFaultString(it)
            }

            if (!faultString.isNullOrBlank()) {
                Log.d("fetchMatieresFamilles", faultString)
                throw Exception(faultString)
            } else {
                throw Exception("Erreur HTTP: ${response.code()}")
            }
        }
    }

    private fun parseChargerRayonFamilleResponse(response: String): Pair<List<Matiere>, List<Famille>> {
        val matieres = mutableListOf<Matiere>()
        val familles = mutableListOf<Famille>()

        // Utilise Jsoup pour parser la réponse XML
        val document = Jsoup.parse(response)

        // Sélectionne les noeuds 'Rayon' pour les matières
        val rayonElements = document.select("LstRayon Rayon")
        rayonElements.forEach { rayonElement ->
            val code = rayonElement.selectFirst("Code")?.text() ?: ""
            val libelle = rayonElement.selectFirst("Libelle")?.text() ?: ""
            matieres.add(Matiere(code, libelle))
        }

        // Sélectionne les noeuds 'Famille'
        val familleElements = document.select("LstFamille Famille")
        familleElements.forEach { familleElement ->
            val code = familleElement.selectFirst("Code")?.text() ?: ""
            val libelle = familleElement.selectFirst("Libelle")?.text() ?: ""
            familles.add(Famille(code, libelle))
        }

        return Pair(matieres, familles)
    }

    override suspend fun fetchQteInventaireVitrine(codeMagasin: String): List<QteInventaireVitrine> {

        // Génère l'enveloppe SOAP
        val request = ChargerQteInventaireVitrine(codeMagasin).buildRequest()

        // Appelle le webservice
        val response = soapService.chargerQteInventaireVitrine(request)

        if (response.isSuccessful) {
            val responseBody = response.body()
                ?: throw Exception("La réponse du webservice ChargerQteInventaireVitrine est vide")
            return parseChargerQteInventaireVitrineResponse(responseBody)
        } else {
            val soapErrorResponse = response.errorBody()?.string()
            var faultString: String? = null

            soapErrorResponse?.let {
                faultString = getFaultString(it)
            }

            if (!faultString.isNullOrBlank()) {
                Log.d("fetchQteInventaireVitrine", faultString)
                throw Exception(faultString)
            } else {
                throw Exception("Erreur HTTP: ${response.code()}")
            }
        }
    }

    private fun parseChargerQteInventaireVitrineResponse(response: String): List<QteInventaireVitrine> {
        val qteInventaireVitrineList = mutableListOf<QteInventaireVitrine>()

        val document = Jsoup.parse(response)

        val qteInventaireVitrineElements =
            document.select("QteInventVitrine")
        qteInventaireVitrineElements.forEach { qteInventaireVitrineElement ->
            val codeInventaire =
                qteInventaireVitrineElement.selectFirst("CodeInventaire")?.text() ?: ""
            val codeVitrine = qteInventaireVitrineElement.selectFirst("CodeVitrine")?.text() ?: ""
            val libVitrine =
                qteInventaireVitrineElement.selectFirst("LibVitrine")?.text() ?: ""
            val qte =
                qteInventaireVitrineElement.selectFirst("Qte")?.text()?.toFloat() ?: 0.toFloat()
            val qteConfie = qteInventaireVitrineElement.selectFirst("QteConfie")?.text()?.toFloat()
                ?: 0.toFloat()
            qteInventaireVitrineList.add(
                QteInventaireVitrine(
                    codeInventaire,
                    codeVitrine,
                    libVitrine,
                    qte,
                    qteConfie
                )
            )
        }
        return qteInventaireVitrineList
    }
}
