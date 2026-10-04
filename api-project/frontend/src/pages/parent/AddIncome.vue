<template>
  <AppNav />
  <div class="main">
    <div class="contents">
      <div class="titleGroup">
        <div class="subTitle">
          <img src="@/assets/images/plus2.png" alt="plus" />
          <p>INCOME ENTRY</p>
        </div>
        <div class="title">
          <p>용돈 주기</p>
        </div>
        <div class="info">
          <p>올바른 경제 관념의 시작, 지금 자녀에게 용돈을 보내보세요.</p>
        </div>
      </div>
      <form @submit.prevent="submit" class="inputForm">
        <div class="left">
          <div class="left-top box">
            <p>금액 입력</p>
            <div class="price">
              <span class="won">&#8361</span>
              <input
                type="number"
                id="amount"
                placeholder="0"
                min="0"
                v-model.number="form.amount"
              />
            </div>
            <div class="priceBtn">
              <div
                v-for="value in QUICK_AMOUNTS"
                :key="value"
                class="btn"
                @click="addAmount(value)"
              >
                <p>+ {{ formatMoney(value) }}</p>
              </div>
            </div>
          </div>
          <div class="left-bottom box">
            <div class="inputBox">
              <div class="categoryBox inputGroup">
                <label for="category">분류</label>
                <select id="category" class="category" v-model="form.incomeType">
                  <option
                    v-for="income in INCOME_TYPES"
                    :key="income.value"
                    :value="income.value"
                  >
                    {{ income.label }}
                  </option>
                </select>
              </div>
              <div class="date inputGroup datetime">
                <label for="transactedAt">일시</label>
                <input
                  type="datetime-local"
                  id="transactedAt"
                  v-model="form.transactedAt"
                />
              </div>
            </div>
            <div class="memoBox">
              <label for="memo">메모</label>
              <input
                type="text"
                placeholder="칭찬 한마디를 남겨주세요."
                id="memo"
                v-model="form.memo"
              />
            </div>
          </div>
        </div>
        <div class="right">
          <div class="right-top box">
            <div class="childInfo" v-if="child">
              <img id="profileImg" :src="iconPath(child.iconId)" />
              <div class="info">
                <p id="childName">{{ child.nickname }}</p>
                <div class="currentBalanceBox">
                  <span>현재 잔액 : </span>
                  &#8361<span id="currentBalance">{{ formatMoney(child.balance) }}</span>
                </div>
              </div>
            </div>
            <div class="money">
              <div id="sendAmount">
                <span class="moneyTitle">금액</span>
                <span>&#8361{{ formatMoney(form.amount) }}</span>
              </div>
            </div>
            <p v-if="message" class="message" :class="messageType">
              {{ message }}
            </p>
            <div class="sendBtnBox">
              <button id="sendBtn" type="submit">용돈 주기</button>
            </div>
          </div>
          <div class="right-bottom box">
            <img src="@/assets/images/idea.png" alt="idea" />
            <div class="tip">
              <p><b>Financial Tip</b></p>
              <p>
                아이와 함께 이번 용돈의 사용 계획을 세워보세요
                저축 목표를 함께 설정하면 경제 관념이 더욱 뚜렷해집니다.
              </p>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import AppNav from '@/components/common/AppNav.vue';
import { fetchChild } from '@/api/children';
import { createIncome } from '@/api/transactions';
import { INCOME_TYPES } from '@/constants/categories';
import {
  formatMoney,
  iconPath,
  nowForInput,
  toApiValue,
} from '@/utils/format';

const QUICK_AMOUNTS = [100, 1000, 10000, 100000];

const route = useRoute();
const childId = route.params.id;

const child = ref(null);
const message = ref('');
const messageType = ref('');

const newForm = () => ({
  incomeType: 'REGULAR',
  amount: 0,
  transactedAt: nowForInput(),
  memo: '',
});
const form = reactive(newForm());

const showMessage = (text, type) => {
  message.value = text;
  messageType.value = type;
};

const loadChild = async () => {
  try {
    child.value = await fetchChild(childId);
  } catch (error) {
    showMessage(error.message, 'error');
  }
};

onMounted(loadChild);

const addAmount = (value) => {
  form.amount = (Number(form.amount) || 0) + value;
};

const submit = async () => {
  if (!form.amount || form.amount <= 0) {
    showMessage('보낼 금액을 정확히 입력해주세요.', 'error');
    return;
  }

  try {
    await createIncome(childId, {
      incomeType: form.incomeType,
      amount: form.amount,
      memo: form.memo,
      transactedAt: form.transactedAt ? toApiValue(form.transactedAt) : null,
    });
    Object.assign(form, newForm());
    await loadChild();
    showMessage('용돈을 성공적으로 보냈어요.', 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  }
};
</script>

<style scoped>
.main {
  width: 100%;
  background-color: #f7f9fb;
  padding-top: 100px;
  padding-bottom: 50px;
}
.contents {
  width: 100%;
  max-width: 1024px;
  box-sizing: border-box;
  margin: 0 auto;
  padding: 48px 24px;
}
.titleGroup {
  margin-bottom: 24px;
}
.subTitle {
  display: flex;
  flex-direction: row;
}
.subTitle > img {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}
.subTitle > p {
  font-weight: bold;
  color: #2456cc;
}
.title > p {
  font-size: 48px;
}

.inputForm {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.left {
  width: 560px;
  max-width: 100%;
  margin: 0;
  padding: 0;
}
.left-top {
  width: 100%;
  min-height: 280px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 32px;
  padding: 32px;
  margin-bottom: 24px;
}

.left-top > p {
  color: #596064;
  font-weight: 500;
  margin-bottom: 24px;
}

.price {
  height: 100px;
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.won {
  font-size: 40px;
  color: #2456cc;
  font-weight: 900;
  margin-right: 15px;
}

#amount {
  font-size: 45px;
  font-weight: 900;
  color: #aeb5b9;
  border: none;
  width: 80%;
  min-width: 0;
  outline: none;
  background-color: none;
}
.priceBtn {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 10px;
}
.btn {
  width: 110px;
  height: 40px;
  border-radius: 50px;
  background-color: #f0f4f7;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.btn > p {
  font-weight: 800;
  font-size: 16px;
  color: #145d42;
}

.left-bottom {
  width: 100%;
  min-height: 315px;
  box-sizing: border-box;
  background-color: #f0f4f7;
  padding: 32px;
  border-radius: 32px;
}
.inputBox {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: space-between;
}
.inputGroup {
  width: 150px;
  display: flex;
  flex-direction: column;
  margin-bottom: 32px;
}

.inputGroup > label {
  font-size: 12px;
  color: #596064;
  margin-bottom: 10px;
}
.inputGroup > select,
.inputGroup > input
{
  border: none;
  width: 100%;
  height: 56px;
  border-radius: 30px;
  padding: 0 20px;
  outline: none;
  box-sizing: border-box;
}

.memoBox {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.memoBox > label {
  font-size: 12px;
  color: #596064;
  margin-bottom: 10px;
}

#memo {
  border: none;
  width: 100%;
  height: 105px;
  border-radius: 30px;
  padding: 5px 20px;
  outline: none;
}

.right {
  width: 390px;
  max-width: 100%;
}
.right-top {
  width: 100%;
  min-height: 290px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 32px;
  padding: 32px;
}
.childInfo {
  display: flex;
  align-items: center;
  height: 64px;
  width: 100%;
  margin-bottom: 24px;
}
#profileImg {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: 3px solid black;
  margin-right: 16px;
}

#sendAmount {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}
.moneyTitle {
  color: #596064;
  font-size: 16px;
}
#sendAmount:last-child {
  color: #2C3437;
  font-weight: 700;
  font-size: 20px;
}

#sendBtn {
  background-color: #3765d2;
  color: white;
  width: 100%;
  height: 70px;
  font-size: 18px;
  border-radius: 35px;
  border: none;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.box {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}

.right-bottom {
  width: 100%;
  background-color: #feb14623;
  padding: 24px;
  margin-top: 24px;
  border-radius: 32px;
  display: flex;
  justify-content: space-around;
}
.right-bottom > img {
  width: 20px;
  height: 20px;
  margin-right: 5px;
}
.tip {
  flex: 1;
  color: #563500;
}

.inputGroup.datetime {
  width: 230px;
}

.message {
  margin: 8px 0 0;
  font-size: 14px;
  text-align: center;
}

.message.success {
  color: #287548;
}

.message.error {
  color: #c24747;
}

@media (max-width: 960px) {
  .inputForm {
    flex-direction: column;
    gap: 24px;
  }

  .left,
  .right {
    width: 100%;
  }
}

@media (max-width: 520px) {
  .main {
    padding-top: 80px;
  }

  .contents {
    padding: 24px 16px;
  }

  .title > p {
    font-size: 36px;
  }

  .left-top,
  .left-bottom,
  .right-top {
    padding: 20px;
  }

  .inputGroup,
  .inputGroup.datetime {
    width: 100%;
    margin-bottom: 8px;
  }
}
</style>
