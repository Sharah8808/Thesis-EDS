package com.thesis.eds.utils

import com.thesis.eds.R
import com.thesis.eds.data.History

object Dummy {
    fun getDummyHistory(): List<History>{
        val listHistory = ArrayList<History>()

        listHistory.add(
            History(
                0,
                "Kazehaya Sakura",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_sakura
            )
        )
        listHistory.add(
            History(
                1,
                "Hanna Zaitseva",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_hanna
            )
        )

        listHistory.add(
            History(
                2,
                "Aoneko Hazuki",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_hazuki
            )
        )
        listHistory.add(
            History(
                3,
                "Kazuhita Izumi",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_izumi
            )
        )
        listHistory.add(
            History(
                4,
                "Kim Namjoon",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_namjoon
            )
        )
        listHistory.add(
            History(
                5,
                "Muraharu Yuuya",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_yuuya
            )
        )
        listHistory.add(
            History(
                6,
                "Natsuya Reiku",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_reiku
            )
        )
        listHistory.add(
            History(
                7,
                "Sean Zaitseva",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_sean
            )
        )
        listHistory.add(
            History(
                8,
                "Irina",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_irina
            )
        )
        listHistory.add(
            History(
                9,
                "Ishikawa Hoseki",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_hoseki
            )
        )
        listHistory.add(
            History(
                10,
                "Tom Scamander",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_tom
            )
        )
        listHistory.add(
            History(
                11,
                "Xarles Flint",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_xarles
            )
        )
        listHistory.add(
            History(
                12,
                "Raven Scamander",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_raven
            )
        )
        listHistory.add(
            History(
                13,
                "Gale Luna Colotte",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_gale
            )
        )
        listHistory.add(
            History(
                14,
                "Evan",
                "Aerotitis Barotrauma",
                "08/08/22",
                R.drawable.dislist_evan
            )
        )

        return listHistory
    }

}