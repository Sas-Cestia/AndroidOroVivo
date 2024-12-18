package fr.cestia.data.webservices.soapservice

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SoapService {
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://tempuri.org/ChargerRayonFamille"
    )
    @POST("WebServiceORV0.asmx") // Endpoint du webservice SOAP
    suspend fun chargerRayonFamille(@Body body: String): Response<String>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://tempuri.org/ChargerQteInventaireVitrine"
    )
    @POST("WebServiceORV0.asmx") // Endpoint du webservice SOAP
    suspend fun chargerQteInventaireVitrine(@Body body: String): Response<String>

}