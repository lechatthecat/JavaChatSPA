import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

// Containers
const DefaultContainer = () => import('@/views/containers/TheContainer');
const OnlyFooter = () => import('@/views/containers/OnlyFooter');
// Views - Pages
// components
const Login = () => import('@/views/pages/Login');
const ForgotPassword = () => import('@/views/pages/ForgotPassword');
const ChangePassword = () => import('@/views/pages/ChangePassword');
//const Page404 = () => import('@/views/pages/Page404');
const Page500 = () => import('@/views/pages/Page500');
const PrivacyPolicy = () => import('@/views/pages/PrivacyPolicy');
const TermOfUse = () => import('@/views/pages/TermOfUse');
const AboutUs = () => import('@/views/pages/AboutUs');
const ConfirmAccount = () => import('@/views/pages/ConfirmAccount');
const CreateUser = () => import('@/views/pages/CreateUser');
const CreateBoard = () => import('@/views/CreateBoard');
const Profile = ()  => import('@/views/Profile');
const Boards = ()  => import('@/views/Boards');
const Chat = ()  => import('@/views/Chat');

Vue.router = new Router({
    history: true,
    mode: 'history', //  https://router.vuejs.org/api/#mode
    linkActiveClass: 'open active',
    scrollBehavior: () => ({ y: 0 }),
    routes: [
        {
            path: '/login',
            component: OnlyFooter,
            meta: {
                auth: false,
                redirect: {name: 'Login'}
            },
            children: [
                {
                    path: '/login',
                    name: 'Login',
                    component: Login,
                    meta: { 
                        auth: false,
                        logout: false,
                        redirect: '/profile',
                    },
                },
                {
                    path: '/logout',
                    name: 'Logout',
                    component: Login,
                    meta: { 
                        auth: false,
                        logout: true,
                    }
                },
                {
                    path: '/create_user',
                    name: 'CreateUser',
                    component: CreateUser,
                    meta: { 
                        auth: false
                    }
                },
            ]
        },
        {
            path: '/confirm_account/:user_token',
            name: 'ConfirmAccount',
            component: ConfirmAccount,
            meta: { 
                auth: false
            }
        },
        {
            path: '/forgot_password/:user_token',
            name: 'ForgotPassword',
            component: ForgotPassword,
            meta: { 
                auth: false
            }
        },
        {
            path: '/change_password/:user_token',
            name: 'ChangePassword',
            component: ChangePassword,
            meta: { 
                auth: false
            }
        },
        {
            path: '/about_us',
            name: 'AboutUs',
            component: OnlyFooter,
            meta: { 
                auth: undefined
            },
            children: [
                {
                    path: '/about_us',
                    name: 'AboutUs',
                    component: AboutUs,
                    meta: { 
                        auth: undefined
                    },
                },
                {
                    path: '/privacy_policy',
                    name: 'PrivacyPolicy',
                    component: PrivacyPolicy,
                    meta: { 
                        auth: undefined
                    },
                },
                {
                    path: '/term_of_use',
                    name: 'TermOfUse',
                    component: TermOfUse,
                    meta: { 
                        auth: undefined
                    },
                },
            ]
        },
        {
            path: '/',
            redirect: '/profile',
            component: DefaultContainer,
            meta: {
                auth: true,
                redirect: {name: 'Login'}
            },
            children: [
                {
                    path: '/profile',
                    name: 'Profile',
                    component: Profile,
                    meta: {
                        auth: true,
                        redirect: {name: 'Login'}
                    }
                },
                {
                    path: '/boards/:category_string_id/:page_number',
                    name: 'Boards',
                    component: Boards,
                    meta: {
                        auth: undefined,
                    }
                },
                {
                    path: '/chat/:category_string_id/:board_string_id/:page_number',
                    name: 'Chat',
                    component: Chat,
                    meta: {
                        auth: undefined,
                    }
                },
                {
                    path: '/board_create',
                    name: 'Board create',
                    component: CreateBoard,
                    meta: {
                        auth: true,
                        redirect: {name: 'Login'}
                    }
                },
            ]
        },
        {
            path: '/error',
            name: 'Page500',
            component: Page500,
            meta: { 
                auth: undefined
            },
        },
        {
            path: '/404',
            name: 'Page404',
            redirect: '/',
        },
    ]
})

export default Vue.router
