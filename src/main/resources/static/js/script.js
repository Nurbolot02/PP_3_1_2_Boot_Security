const addUserPanel = document.querySelector('.add-user-panel');
const userTable = document.querySelector('.user-table');

const addUserPanelButton = document.querySelector('.admin-nav-item-newuser');
const userTableButton = document.querySelector('.admin-nav-item-usertable');

const editUserButtons = document.querySelectorAll('.user-edit-button');
const deleteUserButtons = document.querySelectorAll('.user-delete-button');

const modalBody = document.querySelector('.modal-body');
const modalForm = modalBody.querySelector('.modal-form');
const modalID = modalBody.querySelector('.modal-id');
const modalName = modalBody.querySelector('.modal-name');
const modalSurname = modalBody.querySelector('.modal-surname');
const modalAge = modalBody.querySelector('.modal-age');
const modalUsername = modalBody.querySelector('.modal-username');
const modalCloseButton = modalBody.querySelector('.btn-primary-close');
const modalAcceptButton = modalBody.querySelector('.btn-primary-accept');



const addUserPanelActivate = () => {
    userTableButton.classList.toggle('admin-nav-item-current');
    addUserPanelButton.classList.toggle('admin-nav-item-current');
    userTable.classList.toggle('admin-panel-current');
    addUserPanel.classList.toggle('admin-panel-current');
}

const userTablePanelActivate = () => {
    addUserPanelButton.classList.toggle('admin-nav-item-current');
    userTableButton.classList.toggle('admin-nav-item-current');
    addUserPanel.classList.toggle('admin-panel-current');
    userTable.classList.toggle('admin-panel-current');
}

addUserPanelButton.addEventListener('click', (evt) => {
    evt.preventDefault();
    addUserPanelActivate();
});

userTableButton.addEventListener('click', (evt) => {
    evt.preventDefault();
    userTablePanelActivate();
});

const token = document.querySelector('.csrf').getAttribute('value');
console.log(token)

const deleteUser = (id) => {
    return () => {
        secure_fetch(`/admin/${id}`).then(res => {
            if (res.redirected) {
                window.location.href = res.url
            }
        }).finally()
    }
}

const secure_fetch = (token => {
    const CSRF_HEADER = 'X-CSRF-TOKEN';
    return (url) => {
        const response = fetch(url, {
            method: 'POST',
            redirect: 'follow',
            headers: {
                [CSRF_HEADER]: token
            }
        });
        response.then(res => {
            token = res.headers[CSRF_HEADER]
        });
        return response;
    };
})(token);

deleteUserButtons.forEach(deleteButton =>  {
    deleteButton.addEventListener('click', deleteUser(deleteButton.getAttribute('name')));
})

const modalOpen = (id) => {
    modalBody.classList.remove('modal-hidden');
    const editUser = userTable.querySelector(`.user-${id}`);
    console.log(editUser)
    const editID = editUser.querySelector('.user-id');
    const editName = editUser.querySelector('.user-name');
    const editSurname = editUser.querySelector('.user-surname');
    const editAge = editUser.querySelector('.user-old');
    const editUsername = editUser.querySelector('.user-username');

    modalID.setAttribute('value', editID.textContent);
    modalName.setAttribute('value', editName.textContent)
    modalSurname.setAttribute('value', editSurname.textContent);
    modalAge.setAttribute('value', editAge.textContent);
    modalUsername.setAttribute('value', editUsername.textContent);

    modalCloseButton.addEventListener('click', modalClose);
    modalAcceptButton.addEventListener('click', acceptEdit(id));
}

const modalClose = (evt) => {
    evt.preventDefault()
    modalBody.classList.add('modal-hidden');

    modalCloseButton.removeEventListener('click', modalClose);
    modalAcceptButton.removeEventListener('click', acceptEdit);
}

const editUser = (id) => {
    return () => {
        modalOpen(id);
    }
}

editUserButtons.forEach(editButton => {
    editButton.addEventListener('click', editUser(editButton.getAttribute('name')));
})

const acceptEdit = (id) => {
    // modalForm.setAttribute('action', `/admin/edit/${id}`)
    return () => {
        modalCloseButton.removeEventListener('click', modalClose);
        modalAcceptButton.removeEventListener('click', acceptEdit);
        modalClose();
    }
}