package com.example.pharmcy_app.receiclerView

class DataPharmacie(
    var key: String="",
    var nom: String="",
    var img: String="",
    var adresse:String="",
    var details:String="",
    var longitude: Double=0.0,
    var latitude: Double=0.0
): DataModel(key, nom, img) {
}