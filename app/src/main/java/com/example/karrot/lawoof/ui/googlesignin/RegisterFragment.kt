package com.example.karrot.lawoof.ui.googlesignin

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.karrot.lawoof.R

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }


    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        // TODO: Use the ViewModel


        val paymentSpinner = view!!.findViewById<Spinner>(R.id.spinner_payment)

        if(paymentSpinner != null)
            println("Spinner is da!")

        val paymentArray = arrayOf("Google Pay", "Paypal", "CreditCard", "Bank")

        //val paymentAdapter = ArrayAdapter<String>(view!!.context,  R.layout.payment_spinner, R.id.payment, paymentArray)

        val paymentAdapter = CustomPaymentSpinnerAdapter(view!!.context, R.layout.payment_spinner, R.id.payment, paymentArray.toMutableList(), layoutInflater)

        if(paymentSpinner != null) {
            paymentSpinner.adapter = paymentAdapter

            paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val paymentIcon = view!!.findViewById<ImageView>(R.id.icon)

                    paymentIcon.setImageDrawable(resources.getDrawable(R.drawable.paypal_logo, resources.newTheme()))

                    val paymentText = view.findViewById<TextView>(R.id.payment)
                      when(position){
                          0 -> paymentIcon.setImageResource(R.drawable.ic_google)
                          1 -> paymentIcon.setImageResource(R.drawable.ic_baseline_payment_24px)
                          2 -> paymentIcon.setImageResource(R.drawable.ic_paypal_mono)
                          3 -> paymentIcon.setImageResource(R.drawable.ic_bank)
                      }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }

        }

    }
}

class CustomPaymentSpinnerAdapter(context: Context, resource: Int, textViewResourceId: Int, objects: MutableList<String>, val layoutInflater: LayoutInflater) : ArrayAdapter<String>(context, resource, textViewResourceId, objects) {

    val paymentMethods = arrayOf("GooglePay", "CreditCard", "PayPal", "Bank")

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(position: Int, convertView : View?, parent: ViewGroup): View{
        val inflater = layoutInflater
        val row = inflater.inflate(R.layout.payment_spinner, parent, false)
        val label = row.findViewById<TextView>(R.id.payment)
        label.text = paymentMethods[position]

        val icon = row.findViewById<ImageView>(R.id.icon)

        when(position){
            0 -> icon.setImageResource(R.drawable.ic_google)
            1 -> icon.setImageResource(R.drawable.ic_baseline_payment_24px)
            2 -> icon.setImageResource(R.drawable.ic_paypal_mono)
            3 -> icon.setImageResource(R.drawable.ic_bank)
        }
        return row
    }
}