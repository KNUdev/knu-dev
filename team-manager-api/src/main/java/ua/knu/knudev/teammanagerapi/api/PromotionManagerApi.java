package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

public interface PromotionManagerApi {

    boolean isReadyToPromotion(AccountProfileDto accountProfileDto);
}