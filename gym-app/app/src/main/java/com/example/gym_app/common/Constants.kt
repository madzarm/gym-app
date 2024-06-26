package com.example.gym_app.common


enum class Role {
    ROLE_ADMIN, ROLE_MEMBER, ROLE_TRAINER
}

object AppRoutes {
    const val CALENDAR_SCREEN = "CalendarScreen"
    const val SUBSCRIPTION_SCREEN = "SubscriptionScreen"
    const val STRIPE_ONBOARD_SCREEN = "StripeOnboardScreen"
    const val INVITE_CHALLENGE_SCREEN = "InviteChallengeScreen"
    const val ENTER_FRIEND_CODE_SCREEN = "EnterFriendCodeScreen"
    const val PROFILE_SETUP_SCREEN = "ProfileSetupScreen"
    const val PROFILE_SCREEN = "ProfileScreen"
    const val MEMBER_CHALLENGES_SCREEN = "MemberChallengesScreen"
    const val CREATE_CHALLENGE_SCREEN = "CreateChallengeScreen"
    const val CHALLENGE_DETAILS_SCREEN = "ChallengeDetailsScreen"
    const val MANAGE_CHALLENGES_SCREEN = "ManageChallengesScreen"
    const val CALENDAR_SCREEN_ALL_CLASSES_MEMBER = "CalendarScreenAllClassesMember"
    const val CALENDAR_SCREEN_ALL_CLASSES_TRAINER = "CalendarScreenAllClassesTrainer"
    const val CALENDAR_SCREEN_MEMBER = "CalendarScreenMember"
    const val GYM_CLASS_INSTANCE_SCREEN = "GymClassInstanceScreen"
    const val REVIEW_GYM_CLASS_SCREEN = "ReviewGymClassScreen"
    const val STATISTICS_SCREEN = "StatisticsScreen"
    const val REQUEST_PERMISSION_SCREEN = "RequestPermissionScreen"
    const val QR_CODE_SCREEN = "QrCodeScreen"
    const val GYM_CLASS_DETAILS_SCREEN = "GymClassDetailsScreen"
    const val TRAINER_GYM_CLASS_SCREEN = "TrainerGymClassScreen"
    const val CREATE_CLASS_SCREEN = "CreateClassScreen"
    const val WELCOME_SCREEN = "WelcomeScreen"
    const val SIGNUP_ROLE_SELECTION_SCREEN = "SignupRoleSelectionScreen"
    const val CREATE_ACCOUNT_SCREEN = "CreateAccountScreen"
    const val HOME_SCREEN = "HomePageScreen"
    const val GYM_HOME_SCREEN = "GymHomeScreen"
    const val GROUP_TRAININGS_SCREEN = "GroupTrainingScreen"
    const val LIVE_STATUS_SCREEN = "LiveStatusScreen"
    const val MANAGE_CLASSES_SCREEN = "ManageClassesScreen"
    const val ROLE_SELECTION_SCREEN = "RoleSelectionScreen"
    const val ENTER_GYM_CODE_SCREEN = "EnterGymCodeScreen"
    const val ENTER_TRAINER_ACCESS_CODE_SCREEN = "EnterTrainerAccessCodeScreen"
    const val CREATE_GYM_SCREEN = "CreateGymScreen"
    const val ACCESS_CODE_SCREEN = "AccessCodeScreen"
    const val HOME = "home"
}