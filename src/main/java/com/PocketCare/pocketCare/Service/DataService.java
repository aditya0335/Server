/*
 * Copyright 2020 University at Buffalo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.PocketCare.pocketCare.Service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.PocketCare.pocketCare.Constants.AppConstants;
import com.PocketCare.pocketCare.DAO.DataDAO;
import com.PocketCare.pocketCare.DAO.UserDataDAO;
import com.PocketCare.pocketCare.Entities.ContactListData;
import com.PocketCare.pocketCare.Entities.UserData;
import com.PocketCare.pocketCare.Exception.AuthorizationException;
import com.PocketCare.pocketCare.Exception.ClientException;
import com.PocketCare.pocketCare.Exception.CustomException;
import com.PocketCare.pocketCare.model.DataUploadRequest;
import com.PocketCare.pocketCare.model.DataUploadResponse;

@Service
public class DataService {

	@Autowired
	DataDAO dataDao;

	@Autowired
	UserDataDAO userDataDAO;

	@Autowired
	UserService userService;

	private static final Logger logger = LogManager.getLogger(UserService.class);

	public DataUploadResponse saveContact(String token, DataUploadRequest dataToUpload) throws CustomException{

		UserData user = userService.verifyToken(token);
		if(user == null) {
			logger.debug("saveContact: Token could not be verified. Raising Authorization Exception.");
			throw new AuthorizationException();
		}
		logger.debug("saveContact: Token successfully verified. Saving Contact.");
		List<String> contactList = dataToUpload.getContactList();
		ContactListData contactListData = new ContactListData();
		if (contactList != null && !contactList.isEmpty()) {
			logger.debug("saveContact: Converting VBTNames in contact list to DeviceIds");
			contactListData.setContactList(userDataDAO.convertVBTNameToDeviceId(contactList));
			contactListData.setDeviceId(user.getDeviceId());
			dataDao.saveContact(contactListData);
			logger.debug("saveContact: Successfully saved contact list");
			return new DataUploadResponse(AppConstants.UPLOADSUCCESS, AppConstants.SUCCESS);
		}
		logger.debug("saveContact: Contact List is empty. Raising Client Exception.");
		throw new ClientException();
	}

}
