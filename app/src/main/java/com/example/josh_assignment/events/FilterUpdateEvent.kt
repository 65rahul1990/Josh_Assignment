package com.example.josh_assignment.events

import com.example.josh_assignment.database.PostEntity
import io.realm.RealmResults

class FilterUpdateEvent(val postEntities: RealmResults<PostEntity>)