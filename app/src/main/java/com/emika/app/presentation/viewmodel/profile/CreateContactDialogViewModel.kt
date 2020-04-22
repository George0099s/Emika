package com.emika.app.presentation.viewmodel.profile

import androidx.lifecycle.ViewModel
import com.emika.app.data.network.callback.user.UserInfoCallback
import com.emika.app.data.network.pojo.ModelToken
import com.emika.app.data.network.pojo.updateUserInfo.UpdateUserModel
import com.emika.app.data.network.pojo.user.Contact
import com.emika.app.data.network.pojo.user.Payload
import com.emika.app.di.User
import com.emika.app.domain.repository.profile.UserRepository

class CreateContactDialogViewModel(private val token: String) : ViewModel(),  UserInfoCallback{
    private val repository: UserRepository = UserRepository(token)

    fun addContact(user: User){
        repository.updateUser(user, this)
    }

    override fun updateInfo(model: UpdateUserModel?) {
        print(model!!.ok)
    }

    override fun getUserInfo(userModel: Payload?) {
    }
}
