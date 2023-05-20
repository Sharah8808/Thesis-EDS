package com.thesis.eds.utils

import com.thesis.eds.data.model.DiseaseList

object Dummy {

    fun getDummyDiseaseList(): List<DiseaseList> {
        val listDisease = ArrayList<DiseaseList>()

        listDisease.add(
            DiseaseList("Aerotitis Barotrauma" )
        )
        listDisease.add(
            DiseaseList("Ceruman" )
        )
        listDisease.add(
            DiseaseList("Corpus Alienum" )
        )
        listDisease.add(
            DiseaseList("M Timpani Normal" )
        )
        listDisease.add(
            DiseaseList("Myringitis Bulosa" )
        )
        listDisease.add(
            DiseaseList("OE Difusa" )
        )
        listDisease.add(
            DiseaseList("OE Furunkulosa" )
        )
        listDisease.add(
            DiseaseList("OMA Hiperemis" )
        )
        listDisease.add(
            DiseaseList("OMA Oklusi Tuba" )
        )
        listDisease.add(
            DiseaseList("OMA Perforasi" )
        )
        listDisease.add(
            DiseaseList("OMA Resolusi" )
        )
        listDisease.add(
            DiseaseList("OMA Supurasi" )
        )
        listDisease.add(
            DiseaseList("OMed Efusi" )
        )
        listDisease.add(
            DiseaseList("OMedK Resolusi" )
        )
        listDisease.add(
            DiseaseList("OMedK Tipe Aman" )
        )
        listDisease.add(
            DiseaseList("OMedK Tipe Bahaya" )
        )
        listDisease.add(
            DiseaseList("Otomikosis" )
        )
        listDisease.add(
            DiseaseList("Perforasi Membran Tympani" )
        )
        listDisease.add(
            DiseaseList("Tympanosklerotik" )
        )

        return listDisease
    }

//    fun getDummyDiseaseList(): List<String> {
//        return listOf(
//            "Aerotitis Barotrauma",
//            "Ceruman",
//            "Corpus Alienum",
//            "M Timpani Normal",
//            "Myringitis Bulosa",
//            "Normal",
//            "OE Difusa",
//            "OE Furunkulosa",
//            "OMA Hiperemis",
//            "OMA Oklusi Tuba",
//            "OMA Perforasi",
//            "OMA Resolusi",
//            "OMA Supurasi",
//            "OMed Efusi",
//            "OMedK Resolusi"
//        )
//    }
}