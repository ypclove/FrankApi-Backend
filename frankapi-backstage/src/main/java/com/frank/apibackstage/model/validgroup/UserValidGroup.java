package com.frank.apibackstage.model.validgroup;

import javax.validation.groups.Default;

/**
 * 用户校验分组接口
 *
 * @author Frank
 * @date 2024/6/29
 */
public interface UserValidGroup extends Default {

    interface Crud extends UserValidGroup {

        interface Create extends Crud {
        }

        interface Update extends Crud {
        }

        interface Query extends Crud {
        }

        interface Delete extends Crud {
        }
    }

    interface Login extends UserValidGroup {

        interface PlateLogin extends Login {
        }

        interface EmailLogin extends Login {
        }
    }

    interface Register extends UserValidGroup {

        interface PlateRegister extends Register {
        }

        interface EmailRegister extends Register {
        }
    }

    interface EmailBind extends UserValidGroup {

        interface BindEmail extends EmailBind {
        }

        interface UnBindEmail extends EmailBind {
        }
    }
}
