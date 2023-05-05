package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        int subscriptionPrice=0;
        //calculating subscription total amount
        if(subscription.getSubscriptionType() == SubscriptionType.BASIC){
            subscriptionPrice=500+200*subscription.getNoOfScreensSubscribed();
        } else if (subscription.getSubscriptionType() == SubscriptionType.PRO) {
            subscriptionPrice=800+250*subscription.getNoOfScreensSubscribed();
        } else if (subscription.getSubscriptionType() == SubscriptionType.ELITE) {
            subscriptionPrice=1000+350*subscription.getNoOfScreensSubscribed();
        }

        subscription.setTotalAmountPaid(subscriptionPrice);
        subscription.setUser(user);

        user.setSubscription(subscription);

        userRepository.save(user);

        return subscriptionPrice;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user=userRepository.findById(userId).get();

        if(user.getSubscription().getSubscriptionType() == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        int priceDifferenceToBePaid=0;

        if(user.getSubscription().getSubscriptionType() == SubscriptionType.PRO){
            priceDifferenceToBePaid=1000+350*user.getSubscription().getNoOfScreensSubscribed()-
                    user.getSubscription().getTotalAmountPaid();
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
        } else if (user.getSubscription().getSubscriptionType() == SubscriptionType.BASIC) {
            priceDifferenceToBePaid=800+250*user.getSubscription().getNoOfScreensSubscribed()-
                    user.getSubscription().getTotalAmountPaid();
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
        }
        return priceDifferenceToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription>subscriptionList=subscriptionRepository.findAll();
        int totalRevenue=0;

        for(Subscription subscription:subscriptionList){
            totalRevenue+=subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
