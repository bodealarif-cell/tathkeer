package com.example.tathkeer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tathkeer.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val prefs: Preferences) : ViewModel() {

    // الأذكار الأصلية (ثابتة)
    private val morningBase = AdhkarProvider.morningAdhkar
    private val eveningBase = AdhkarProvider.eveningAdhkar

    // التدفقات (states)
    private val _section = MutableStateFlow("morning")
    val section: StateFlow<String> = _section.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _morningDone = MutableStateFlow<List<Int>>(emptyList())
    private val _eveningDone = MutableStateFlow<List<Int>>(emptyList())

    // القائمة الحالية مع دمج التقدم
    val currentList: StateFlow<List<Zikr>> = combine(
        _section,
        _morningDone,
        _eveningDone
    ) { sec, mDone, eDone ->
        val base = if (sec == "morning") morningBase else eveningBase
        val doneList = if (sec == "morning") mDone else eDone
        if (doneList.size != base.size) base
        else base.mapIndexed { i, zikr -> zikr.copy(done = doneList[i]) }
    }.stateFlow(viewModelScope, morningBase)

    // الذكر الحالي
    val currentZikr: StateFlow<Zikr?> = combine(currentList, index) { list, idx ->
        list.getOrNull(idx)
    }.stateFlow(viewModelScope, null)

    init {
        viewModelScope.launch {
            prefs.sectionFlow.collect { _section.value = it }
        }
        viewModelScope.launch {
            prefs.indexFlow.collect { _index.value = it }
        }
        viewModelScope.launch {
            prefs.morningDoneFlow().collect { _morningDone.value = it }
        }
        viewModelScope.launch {
            prefs.eveningDoneFlow().collect { _eveningDone.value = it }
        }
    }

    fun increment() {
        val sec = _section.value
        val idx = _index.value
        val list = if (sec == "morning") morningBase else eveningBase
        if (idx !in list.indices) return

        val doneList = if (sec == "morning") _morningDone.value.toMutableList() else _eveningDone.value.toMutableList()
        if (doneList.size != list.size) {
            // تهيئة إذا كانت فارغة
            doneList.clear()
            doneList.addAll(List(list.size) { 0 })
        }

        if (doneList[idx] < list[idx].count) {
            doneList[idx] = doneList[idx] + 1
            updateDoneList(sec, doneList)

            // إذا اكتمل، انتقل تلقائياً للتالي
            if (doneList[idx] >= list[idx].count && idx < list.size - 1) {
                setIndex(idx + 1)
            }
        } else {
            // إذا كان مكتملاً، انتقل للتالي
            if (idx < list.size - 1) setIndex(idx + 1)
        }
    }

    fun setIndex(newIndex: Int) {
        if (newIndex in 0 until (if (_section.value == "morning") morningBase.size else eveningBase.size)) {
            _index.value = newIndex
            viewModelScope.launch { prefs.saveIndex(newIndex) }
        }
    }

    fun setSection(newSection: String) {
        if (newSection == _section.value) return
        _section.value = newSection
        _index.value = 0
        viewModelScope.launch {
            prefs.saveSection(newSection)
            prefs.saveIndex(0)
        }
    }

    fun resetAll() {
        val sec = _section.value
        val size = if (sec == "morning") morningBase.size else eveningBase.size
        val zeroList = List(size) { 0 }
        updateDoneList(sec, zeroList)
        setIndex(0)
    }

    private fun updateDoneList(sec: String, newList: List<Int>) {
        if (sec == "morning") {
            _morningDone.value = newList
            viewModelScope.launch {
                val zikrList = morningBase.mapIndexed { i, z -> z.copy(done = newList[i]) }
                prefs.saveMorningDone(zikrList)
            }
        } else {
            _eveningDone.value = newList
            viewModelScope.launch {
                val zikrList = eveningBase.mapIndexed { i, z -> z.copy(done = newList[i]) }
                prefs.saveEveningDone(zikrList)
            }
        }
        // تحديث الـ widget
        // سيتم تنفيذ تحديث الـ widget في مكان آخر (في Activity أو Worker)
    }
}
