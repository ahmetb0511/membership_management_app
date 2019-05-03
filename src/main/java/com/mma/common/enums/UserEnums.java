package com.mma.common.enums;

public class UserEnums {

	public enum Role {
		Administrator, Moderator, User
	}
	
	public enum Status {
		ENABLED, DISABLED, DELETED
	}

	public enum Category {
		USERS("Korisnici"),
		REPORTS("Izvještaj"),
		FEE_TYPES("Vrste uplata"),
		FEES("Uplate");

		private String value;

		private Category(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
