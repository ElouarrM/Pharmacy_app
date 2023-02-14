package com.example.pharmcy_app.receiclerView


class DataMedicament(
 var key: String="",
 var nom: String="",
 var img: String="",
 var prix: Int=0,
 var nbrAchat: Int=0,
 var details:String="",
 var titlePharmacie:String="",
 var stock:Int=0 ) : DataModel(key, nom, img) {

 }